
import os
import io
import json
import traceback
from dotenv import load_dotenv
from fastapi import FastAPI, File, UploadFile, Form, HTTPException
from pydantic import BaseModel, field_validator
import PyPDF2
import uvicorn

from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage

# --- Environment and API Key Setup ---
load_dotenv()
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

if not GEMINI_API_KEY:
    raise ValueError("GEMINI_API_KEY not found. Please set it in your .env file.")

# --- Pydantic Models ---
class ATSAnalysis(BaseModel):
    match_percentage: int
    missing_keywords: list[str]
    profile_summary: str
    improvement_tips: list[str]
    application_success_rate: str

    @field_validator('match_percentage')
    def score_must_be_in_range(cls, v):
        if not 0 <= v <= 100:
            raise ValueError('Match percentage must be between 0 and 100')
        return v

class ChatMessage(BaseModel):
    role: str
    content: str

class InterviewRequest(BaseModel):
    messages: list[ChatMessage]
    job_role: str

class InterviewResponse(BaseModel):
    response: str

# --- FastAPI Application Initialization ---
app = FastAPI(
    title="Resume ATS Score Checker API",
    description="A backend that analyzes a resume against a job description and provides mock interviews.",
    version="1.1.0",
)

# --- AI Setup ---
llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash",
    google_api_key=GEMINI_API_KEY,
    temperature=0.7,
)

# --- Helper Functions ---
def extract_text_from_pdf(pdf_file: io.BytesIO) -> str:
    try:
        pdf_reader = PyPDF2.PdfReader(pdf_file)
        text = ""
        for page in pdf_reader.pages:
            page_text = page.extract_text()
            if page_text:
                text += page_text + "\n"
        if not text.strip():
            raise HTTPException(status_code=400, detail="Could not extract text from PDF.")
        return text
    except Exception as e:
        print(f"PDF Error: {e}")
        raise HTTPException(status_code=500, detail=f"PDF Processing Error: {str(e)}")

def get_gemini_analysis(resume_text: str, job_description: str) -> dict:
    prompt = f'''
    Act as an expert ATS. Analyze the resume against the job description.
    Return ONLY a JSON object with these keys: match_percentage (int), missing_keywords (list), profile_summary (str), improvement_tips (list), application_success_rate (str).

    Resume: {resume_text}
    JD: {job_description}
    '''
    try:
        message = HumanMessage(content=prompt)
        response = llm.invoke([message])
        raw_content = response.content.strip()
        if raw_content.startswith("```json"):
            raw_content = raw_content[7:-3].strip()
        elif "{" in raw_content:
             # Basic fallback to find JSON block if model gets chatty
             start = raw_content.find("{")
             end = raw_content.rfind("}") + 1
             raw_content = raw_content[start:end]

        return json.loads(raw_content)
    except Exception as e:
        print(f"Analysis Error: {e}")
        raise HTTPException(status_code=502, detail=f"AI Error: {str(e)}")

# --- Endpoints ---

@app.post("/analyze-resume", response_model=ATSAnalysis)
async def analyze_resume_endpoint(
    resume_file: UploadFile = File(...),
    job_description: str = Form(...)
):
    pdf_content = await resume_file.read()
    resume_text = extract_text_from_pdf(io.BytesIO(pdf_content))
    analysis_data = get_gemini_analysis(resume_text, job_description)
    return ATSAnalysis(**analysis_data)

@app.post("/interview", response_model=InterviewResponse)
async def mock_interview_endpoint(request: InterviewRequest):
    try:
        # Construct message history with proper roles
        chat_history = []
        chat_history.append(SystemMessage(content=f"Act as a professional interviewer for a {request.job_role} position. Ask one question at a time. Stay in character."))

        for msg in request.messages:
            if msg.role.lower() == "user":
                chat_history.append(HumanMessage(content=msg.content))
            else:
                chat_history.append(AIMessage(content=msg.content))

        response = llm.invoke(chat_history)
        return InterviewResponse(response=response.content.strip())
    except Exception as e:
        print("--- INTERVIEW ENDPOINT ERROR ---")
        traceback.print_exc()
        raise HTTPException(status_code=502, detail=f"AI Error: {str(e)}")

@app.get("/")
def health_check():
    return {"status": "ok"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=5000)
