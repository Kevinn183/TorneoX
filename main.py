from flask import Flask, jsonify, request
from pymongo import MongoClient

MONGO_URI = "mongodb+srv://kevarebri:zlLWamJR6TCEPVeI@Cluster0/TorneoX?retryWrites=true&w=majority"
client = MongoClient(MONGO_URI)
db = client.get_default_database()

app = Flask(__name__)

@app.route('/usuarios', methods=['POST'])
def crear_usuario():
    nuevo_usuario = request.json
    db.usuarios.insert_one(nuevo_usuario)
    return jsonify({"mensaje": "Usuario creado correctamente"}), 201