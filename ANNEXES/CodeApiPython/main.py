from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
from recommendation import generate_recommendations
from models import UserData

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Online recommendation API"}

@app.post("/recommendations")
async def get_recommendations(data: UserData):
    try:
        print(f"Request received for user ID: {data.user_id}")
        recommendations = generate_recommendations(data)
        return {"user_id":data.user_id, "recommendations": recommendations}
    except Exception as e:
        print("Error during the generation of recommendations:", e)
        raise HTTPException(status_code=500, detail=str(e))