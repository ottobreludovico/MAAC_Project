from flask import Flask,jsonify
from flask_restful import Api,Resource,reqparse

app = Flask(__name__)
api = Api(app)

parserget = reqparse.RequestParser()

parserpost = reqparse.RequestParser()
parserpost.add_argument('title')
parserpost.add_argument('description')
parserpost.add_argument('ima')
parserpost.add_argument('note')


parsergetI = reqparse.RequestParser()

parserpostI = reqparse.RequestParser()
parserpostI.add_argument('name')
parserpostI.add_argument('date')
parserpostI.add_argument('image')


parserpostE = reqparse.RequestParser()
parserpostE.add_argument('title')
parserpostE.add_argument('description')
parserpostE.add_argument('ima')
parserpostE.add_argument('lat')
parserpostE.add_argument('lon')
parserpostE.add_argument('rate')

parserpostU = reqparse.RequestParser()
parserpostU.add_argument('note')

parserpostIE = reqparse.RequestParser()
parserpostIE.add_argument('name')
parserpostIE.add_argument('date')
parserpostIE.add_argument('image')

MovieDB = []

LastID = 0

ItineraryDB = []

LastIDI = 0
LastIDE = 0

class Movies(Resource):
    def get(self):
        global MovieDB
        return MovieDB

    def post(self):
        global parserpost,LastID
        args = parserpost.parse_args()
        LastID+=1
        movie={}
        movie["title"]=args['title']
        movie["description"]=args['description']
        movie["ima"]=args['ima']
        movie["note"]=args['note']
        movie["id"]=LastID
        MovieDB.insert(LastID,movie)
        return {"id":LastID}

    @app.route('/<id>',methods=['DELETE'])
    def delete(id):
        for elem in MovieDB:
            if elem["id"]==int(id):
                MovieDB.remove(elem)
        return "{'error':False}"

    @app.route('/<id>',methods=['PUT'])
    def put(id):
        global parserpostU
        args = parserpost.parse_args()
        for elem in MovieDB:
            if elem["id"]==int(id):
                elem["note"]=args["note"]
                return elem
        return "{'error':False}"

    @app.route('/<id>',methods=['GET'])
    def get2(id):
        m = None
        for elem in MovieDB:
            if elem["id"]==int(id):
                m=elem
        if m!=None:
            return m
        else:
            return "{'error':False}"


class Itineraries(Resource):
    def get(self):
        global ItineraryDB
        return ItineraryDB

    def post(self):
        global parserpostI,LastIDI
        args = parserpostI.parse_args()
        LastIDI+=1
        movie={}
        movie["name"]=args['name']
        movie["image"]=args['image']
        movie["date"]=args['date']
        movie["list"]=[]
        movie["id"]= LastIDI
        ItineraryDB.insert(LastIDI,movie)
        return {"id":LastIDI}

    @app.route('/itineraries/<id>',methods=['DELETE'])
    def delete3(id):
        for elem in ItineraryDB:
            if elem["id"]==int(id):
                ItineraryDB.remove(elem)
        return "{'error':False}"

    @app.route('/itineraries/<id>',methods=['PUT'])
    def put3(id):
        global parserpostIE
        args = parserpostIE.parse_args()
        for elem in ItineraryDB:
            if elem["id"]==int(id):
                elem["name"]=args["name"]
                elem["date"]=args["date"]
                return elem
        return "{'error':False}"

    @app.route('/itineraries/<id>',methods=['POST'])
    def post3(id):
        global parserpostE,LastIDE
        args = parserpostE.parse_args()
        LastIDE+=1
        movie={}
        movie["title"]=args['title']
        movie["description"]=args['description']
        movie["ima"]=args['ima']
        movie["lat"]=args['lat']
        movie["lon"]=args['lon']
        movie["rate"]=args['rate']
        movie["id"]=LastIDE
        m = None
        for elem in ItineraryDB:
            if elem["name"]==id:
                m=elem
        if m!=None:
            m["list"].insert(LastIDE,movie)
            return m
        else:
            return "{'error':False}"
        

    @app.route('/itineraries/<id>',methods=['GET'])
    def get3(id):
        m = None
        for elem in ItineraryDB:
            if elem["id"]==int(id):
                m=elem
        if m!=None:
            return jsonify(m["list"])
        else:
            return "{'error':False}"


def seed(howmany):
    global LastID
    for i in range(0,howmany):
        movie = {'id':LastID,'title':"A movie",'description':"A description",'rate':5}
        MovieDB.append(movie)
        LastID+=1

api.add_resource(Movies,'/')
api.add_resource(Itineraries,'/itineraries/')

if __name__ == '__main__':
    print ('starting REST server...')
    seed(2)
    app.run(host='0.0.0.0')
