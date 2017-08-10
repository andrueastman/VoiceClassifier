from flask import Flask, jsonify, request, abort
import Utils as myUtils
import sqlite3 as sql

app = Flask(__name__)

@app.route('/')
def home():
    return 'You are home.'

@app.route('/backend/api/v1.0/feedback/<id>')
def say_hi(id):
    #return str(id)    
    id = int(id)
    statement = "UPDATE data SET valid = 0 WHERE id = %s" %(id,)
    #return statement
    con = sql.connect("/var/www/FlaskApp/FlaskApp/database.db") 
    cur = con.cursor() 
    cur.execute(statement)
    con.commit()     
    con.close()
    return 'OK'



@app.route('/backend/api/v1.0/classifier', methods=['POST'])
def create_task():
    if not request.json or not '0' in request.json:
        abort(400)

    values = request.json;  # json data
    data = list()  # empty list for data
    count = 0  # index pointer

    while str(count) in values:
        #print(values[str(count)])
        data.append(float(values[str(count)]))
        count = count + 1

   
    mean=myUtils.calculate_mean(data)
    maximum = max(data)
    minimum = min(data)	    
    
    #logistic regresion model made from weka with data set. Achieved a 95.928 accuracy on the proviced dataset from voice samples
	
    meanCoefficient = -198.1909
    minCoefficient = 49.0034
    maxCoefficient = 13.2093
    intercept = 22.6508

    boundary = intercept + (meanCoefficient*mean) + (minCoefficient*minimum) + (maxCoefficient*maximum)
    
    gender = "Female"

    if mean < 140:
        gender="Male"
    elif mean < 180:
        if boundary > 0:
            gender = "Male"
   	
    #result ={'gender':gender}
    
    con = sql.connect("/var/www/FlaskApp/FlaskApp/database.db")         
    cur = con.cursor()         
    cur.execute("INSERT INTO data (mean,max,min,data,gender) VALUES (?,?,?,?,?)", (str(mean),str(maximum),str(minimum),str(data),gender))         
    record_id = cur.lastrowid
    con.commit()         
    con.close()		    

    result = {'gender':gender,'id':record_id}	

    return jsonify(results = result)


if __name__ == '__main__':
    app.run(host='0.0.0.0')
