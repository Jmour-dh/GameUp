from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
from recommendation import generate_recommendations
from models import UserData

app = FastAPI()

# Endpoint de base pour tester que l'API est en ligne
@app.get("/")
async def root():
    return {"message": "API de recommandation en ligne"}

# Endpoint pour envoyer les données d'utilisateur et récupérer des recommandations
@app.post("/recommendations")
async def get_recommendations(data: UserData):
    try:
        print(f"Requête reçue pour l'utilisateur ID: {data.user_id}") 
        recommendations = generate_recommendations(data)
        return {"user_id":data.user_id, "recommendations": recommendations}
    except Exception as e:
        print("Erreur lors de la génération des recommandations :", e)
        raise HTTPException(status_code=500, detail=str(e))