import socket
import pymongo
import json

rev_dic_of_quizzes = {"Genesis": "בראשית", "Exodus": "שמות", "Leviticus": "ויקרא", "Numbers": "במדבר", "Deuteronomy": "דברים"}
dic_of_quizzes2 = {"quizzes": ["בראשית", "שמות", "ויקרא", "במדבר", "דברים"]}
dic_of_quizzes = {"בראשית": "Genesis", "שמות": "Exodus", "ויקרא": "Leviticus", "במדבר": "Numbers", "דברים": "Deuteronomy"}
jsonString = ('{ "id": "216344754", "first name": "בוני", "last name": "בל", '
              '"email": "boni@gmail.com", "school": "גימנסיה הריאלית",'
              ' "password": "boni123", "job": "teacher"}')
SERVER_IP = '0.0.0.0'
DEST_PORT = 5000
letters = "abcdefghijklmnopqrstuvwxyzABCDEFJHIJKLMNOPQRSTUVWXYZ"


def crack(encrypt, key):
    newM = ""
    for i in range(0, len(encrypt)):
        if encrypt[i] not in letters:
            newM += encrypt[i]
        else:
            place = letters.index(encrypt[i]) - key
            if place < 0:
                place += len(letters)
            newM += letters[place]
    return newM


def encryption(message, key):
    newM = ""
    for i in range(0, len(message)):
        place = letters.index(message[i]) + key
        if place > len(letters):
            place = place - len(letters)
        if 0 < place < len(letters):
            newM = newM + letters[place]
    return newM


def sign_up(info):
    connection = pymongo.MongoClient('localhost', 27017)
    student_details = json.loads(info)
    if str(student_details.get("job")) == "teacher":
        database = connection["userT"]
        collection = database["teachers"]
        if collection.count_documents({"_id": student_details.get("id")}, limit=1) != 0:
            return False
        data = {
            '_id': str(student_details.get("id")),
            'full name': str(student_details.get("first name") + " " + student_details.get("last name")),
            'email': str(student_details.get("email")),
            'school': str(student_details.get("school")),
            'password': str(student_details.get("password")),
            'assigned quizzes': [["none", 1]]
        }
    else:
        database = connection["userS"]
        collection = database["students"]
        if collection.count_documents({"_id": student_details.get("id")}, limit=1) != 0:
            return False
        data = {
            '_id': str(student_details.get("id")),
            'full name': str(student_details.get("first name") + " " + student_details.get("last name")),
            'email': str(student_details.get("email")),
            'school': str(student_details.get("school")),
            'password': str(student_details.get("password")),
            'quizzes complete': [["none", 1]]
        }

    collection.insert_one(data)
    return True


def add_quiz(quiz):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[quiz]
    array = list(collection.find())
    if len(array) == 0:
        num = 0
    else:
        num = array[len(array) - 1].get("quiz num")
    questions = {"quiz num": int(num) + 1,
                 "question 1": {"question": "", "answers": {}, "correct answer": ""},
                 "question 2": {"question": "", "answers": {}, "correct answer": ""},
                 "question 3": {"question": "", "answers": {}, "correct answer": ""},
                 "question 4": {"question": "", "answers": {}, "correct answer": ""},
                 "question 5": {"question": "", "answers": {}, "correct answer": ""},
                 "question 6": {"question": "", "answers": {}, "correct answer": ""},
                 "question 7": {"question": "", "answers": {}, "correct answer": ""},
                 "question 8": {"question": "", "answers": {}, "correct answer": ""},
                 "question 9": {"question": "", "answers": {}, "correct answer": ""},
                 "question 10": {"question": "", "answers": {}, "correct answer": ""}}
    for i in range(1, 11):
        questions["question " + str(i)]["question"] = input("Enter question number " + str(i) + ": ")
        answers = {"answer 1": "", "answer 2": "", "answer 3": "", "answer 4": ""}
        for j in range(1, 5):
            answers["answer " + str(j)] = input("Enter possible answer number " + str(j) + ": ")
        questions["question " + str(i)]["answers"] = answers
        questions["question " + str(i)]["correct answer"] = input("Enter the number of the correct answer: ")
    collection.insert_one(questions)
    print(questions)
    print("Quiz added successfully")


def complete_quiz(quiz, num, id_num):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["userS"]
    collection = database["students"]
    if collection.find_one({'_id': id_num}).get("quizzes complete")[0][0] == "none":
        collection.update_one({'_id': id_num}, {"$set": {"quizzes complete": [[quiz, num]]}})
    else:
        list1 = collection.find_one({'_id': id_num}).get("quizzes complete")
        if [dic_of_quizzes[quiz], num] not in list1:
            list1 += [[dic_of_quizzes[quiz], num]]
            collection.update_one({'_id': id_num}, {"$set": {"quizzes complete": list1}})


def assigned_quizzes(quiz, num, id_num):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["userT"]
    collection = database["teachers"]
    if collection.find_one({'_id': id_num}).get("assigned quizzes")[0][0] == "none":
        collection.update_one({'_id': id_num}, {"$set": {"assigned quizzes": [[quiz, num]]}})
    else:
        list1 = collection.find_one({'_id': id_num}).get("assigned quizzes")
        if [quiz, num] not in list1:
            list1 += [[quiz, num]]
            collection.update_one({'_id': id_num}, {"$set": {"assigned quizzes": list1}})
    print(collection.find_one({'_id': id_num}))


def get_answers(quiz_name, num):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[dic_of_quizzes[quiz_name]]
    quiz = dict(collection.find_one({"quiz num": num}))
    answers = []
    for i in range(1, 11):
        answers += [quiz["question " + str(i)]["correct answer"]]
    return "/".join(map(str, answers)) + "\n"


def getDialog(quiz_name, num, num_question, num_answer):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[dic_of_quizzes[quiz_name]]
    quiz = dict(collection.find_one({"quiz num": num}))
    question = quiz.get("question " + str(num_question))
    dialog = question.get("question") + "/"
    if num_answer == 0:
        dialog += "לא סימנת תשובה"
    else:
        dialog += question.get("answers").get("answer " + str(num_answer))
    dialog += "/" + question.get("answers").get("answer " + question.get("correct answer")) + "\n"
    return dialog


def what_quizzes_left(id_num):
    connection1 = pymongo.MongoClient('localhost', 27017)
    database1 = connection1["userS"]
    collection1 = database1["students"]
    connection2 = pymongo.MongoClient('localhost', 27017)
    database2 = connection2["userT"]
    collection2 = database2["teachers"]
    student = collection1.find_one({"_id": id_num})
    school = student.get("school")
    complete = student.get("quizzes complete")
    assigned = collection2.find_one({"school": school}).get("assigned quizzes")
    left = []
    for i in assigned:
        if i not in complete:
            left += i
    print(left)


def is_signIn(info):
    connection = pymongo.MongoClient('localhost', 27017)
    details = json.loads(info)
    database = connection["userT"]
    collection = database["teachers"]
    if collection.count_documents({"_id": details.get("id")}, limit=1) == 1:
        if collection.find_one({"_id": details.get("id")}).get("password") != details.get("password"):
            return "false"
        return "teacher"
    database = connection["userS"]
    collection = database["students"]
    if collection.count_documents({"_id": details.get("id")}, limit=1) == 1:
        if collection.find_one({"_id": details.get("id")}).get("password") != details.get("password"):
            return "false"
        return "student"
    return "false"


def get_info(data):
    connection = pymongo.MongoClient('localhost', 27017)
    details = json.loads(data)
    database = connection["userT"]
    collection = database["teachers"]
    job = "מורה"
    if collection.count_documents({"_id": details.get("id")}, limit=1) == 0:
        database = connection["userS"]
        collection = database["students"]
        job = "תלמיד"
    dic = collection.find_one({"_id": details.get("id")})
    return dic.get("full name") + "," + dic.get("school") + "," + dic.get("email") + "," + job


def get_amount_of_questions(quiz_name):
    if quiz_name not in dic_of_quizzes:
        return 0
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[dic_of_quizzes[quiz_name]]
    return str(collection.count_documents({})) + "\n"


def get_collections(quiz_name):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection[quiz_name]
    names = ""
    for i in dic_of_quizzes2[quiz_name]:
        names += i + "/"
    return names + "\n"


if __name__ == '__main__':
    while True:
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.bind((SERVER_IP, DEST_PORT))
        server_socket.listen(1)
        client_socket, address = server_socket.accept()
        data = client_socket.recv(2048).decode()
        print(data)
        #data = crack(data, 2) #רק בהרשמה
        info = json.loads(data)
        if info.get("function") == "signUp":
            if sign_up(data):
                client_socket.sendall(encryption("true", 3).encode())
            else:
                client_socket.sendall(encryption("false", 3).encode())
        elif info.get("function") == "signIn":
            what = is_signIn(data)
            print(what)
            if what == "teacher" or what == "student":
                client_socket.sendall("true\n".encode())
            else:
                client_socket.sendall("false\n".encode())
        elif info.get("function") == "getInfo":
            print(get_info(data))
            infoS = get_info(data) + "\n"
            client_socket.sendall(infoS.encode())
        elif info.get("function") == "getQuiz":
            connection = pymongo.MongoClient('localhost', 27017)
            database = connection["quizzes"]
            collection = database[dic_of_quizzes[info.get("quiz")]]
            dic1 = collection.find_one({"quiz num": info.get("quiz num")})
            print(dic1)
            print(info.get("question num"))
            dic2 = dic1.get("question " + str(info.get("question num")))
            print(dic2)
            question = dic2.get("question") + "/" + dic2.get("answers").get("answer 1") + "/" + dic2.get("answers").get(
                'answer 2') + "/" + dic2.get("answers").get("answer 3") + "/" + dic2.get("answers").get("answer 4") + "\n"
            client_socket.sendall(question.encode())
        elif info.get("function") == "getAnswers":
            print(get_answers(info.get("quiz"), info.get("quiz num")))
            client_socket.sendall(get_answers(info.get("quiz"), info.get("quiz num")).encode())
        elif info.get("function") == "dialog":
            print(info)
            print(getDialog(info.get("quiz"), info.get("quiz num"), info.get("question num"), info.get("answer")))
            client_socket.sendall(getDialog(info.get("quiz"), info.get("quiz num"),
                                            info.get("question num"), info.get("answer")).encode())
        elif info.get("function") == "howManyQuizzes":
            client_socket.sendall(get_amount_of_questions(info.get("quiz_name")).encode())
        elif info.get("function") == "getCollections":
            client_socket.sendall(get_collections(info.get("quiz")).encode())
        elif info.get("function") == "completeQuiz":
            complete_quiz(info.get("quiz"), info.get("quiz num"), info.get("id"))
            client_socket.sendall("true".encode())

        client_socket.close()
        server_socket.close()
import socket
import pymongo
import json
from bson import ObjectId

rev_dic_of_quizzes = {"Genesis": "בראשית", "Exodus": "שמות", "Leviticus": "ויקרא", "Numbers": "במדבר", "Deuteronomy": "דברים"}
dic_of_quizzes2 = {"quizzes": ["בראשית", "שמות", "ויקרא", "במדבר", "דברים"]}
dic_of_quizzes = {"בראשית": "Genesis", "שמות": "Exodus", "ויקרא": "Leviticus", "במדבר": "Numbers", "דברים": "Deuteronomy"}
jsonString = ('{ "id": "216344754", "first name": "בוני", "last name": "בל", '
              '"email": "boni@gmail.com", "school": "גימנסיה הריאלית",'
              ' "password": "boni123", "job": "teacher"}')
SERVER_IP = '0.0.0.0'
DEST_PORT = 5000
letters = "abcdefghijklmnopqrstuvwxyzABCDEFJHIJKLMNOPQRSTUVWXYZ"


def crack(encrypt, key):
    newM = ""
    for i in range(0, len(encrypt)):
        if encrypt[i] not in letters:
            newM += encrypt[i]
        else:
            place = letters.index(encrypt[i]) - key
            if place < 0:
                place += len(letters)
            newM += letters[place]
    return newM


def encryption(message, key):
    newM = ""
    for i in range(0, len(message)):
        place = letters.index(message[i]) + key
        if place > len(letters):
            place = place - len(letters)
        if 0 < place < len(letters):
            newM = newM + letters[place]
    return newM


def sign_up(info):
    connection = pymongo.MongoClient('localhost', 27017)
    student_details = json.loads(info)
    if str(student_details.get("job")) == "teacher":
        database = connection["userT"]
        collection = database["teachers"]
        if collection.count_documents({"_id": student_details.get("id")}, limit=1) != 0:
            return False
        data = {
            '_id': str(student_details.get("id")),
            'full name': str(student_details.get("first name") + " " + student_details.get("last name")),
            'email': str(student_details.get("email")),
            'school': str(student_details.get("school")),
            'password': str(student_details.get("password")),
            'assigned quizzes': [["none", 1]]
        }
    else:
        database = connection["userS"]
        collection = database["students"]
        if collection.count_documents({"_id": student_details.get("id")}, limit=1) != 0:
            return False
        data = {
            '_id': str(student_details.get("id")),
            'full name': str(student_details.get("first name") + " " + student_details.get("last name")),
            'email': str(student_details.get("email")),
            'school': str(student_details.get("school")),
            'password': str(student_details.get("password")),
            'quizzes complete': [["none", 1]]
        }

    collection.insert_one(data)
    return True


def add_quiz(quiz):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[quiz]
    array = list(collection.find())
    if len(array) == 0:
        num = 0
    else:
        num = array[len(array) - 1].get("quiz num")
    questions = {"quiz num": int(num) + 1,
                 "question 1": {"question": "", "answers": {}, "correct answer": ""},
                 "question 2": {"question": "", "answers": {}, "correct answer": ""},
                 "question 3": {"question": "", "answers": {}, "correct answer": ""},
                 "question 4": {"question": "", "answers": {}, "correct answer": ""},
                 "question 5": {"question": "", "answers": {}, "correct answer": ""},
                 "question 6": {"question": "", "answers": {}, "correct answer": ""},
                 "question 7": {"question": "", "answers": {}, "correct answer": ""},
                 "question 8": {"question": "", "answers": {}, "correct answer": ""},
                 "question 9": {"question": "", "answers": {}, "correct answer": ""},
                 "question 10": {"question": "", "answers": {}, "correct answer": ""}}
    for i in range(1, 11):
        questions["question " + str(i)]["question"] = input("Enter question number " + str(i) + ": ")
        answers = {"answer 1": "", "answer 2": "", "answer 3": "", "answer 4": ""}
        for j in range(1, 5):
            answers["answer " + str(j)] = input("Enter possible answer number " + str(j) + ": ")
        questions["question " + str(i)]["answers"] = answers
        questions["question " + str(i)]["correct answer"] = input("Enter the number of the correct answer: ")
    collection.insert_one(questions)
    print(questions)
    print("Quiz added successfully")


def complete_quiz(quiz, num, id_num):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["userS"]
    collection = database["students"]
    if collection.find_one({'_id': id_num}).get("quizzes complete")[0][0] == "none":
        collection.update_one({'_id': id_num}, {"$set": {"quizzes complete": [[quiz, num]]}})
    else:
        list1 = collection.find_one({'_id': id_num}).get("quizzes complete")
        if [dic_of_quizzes[quiz], num] not in list1:
            list1 += [[dic_of_quizzes[quiz], num]]
            collection.update_one({'_id': id_num}, {"$set": {"quizzes complete": list1}})


def assigned_quizzes(quiz, num, id_num):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["userT"]
    collection = database["teachers"]
    if collection.find_one({'_id': id_num}).get("assigned quizzes")[0][0] == "none":
        collection.update_one({'_id': id_num}, {"$set": {"assigned quizzes": [[quiz, num]]}})
    else:
        list1 = collection.find_one({'_id': id_num}).get("assigned quizzes")
        if [quiz, num] not in list1:
            list1 += [[quiz, num]]
            collection.update_one({'_id': id_num}, {"$set": {"assigned quizzes": list1}})
    print(collection.find_one({'_id': id_num}))


def get_answers(quiz_name, num):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[dic_of_quizzes[quiz_name]]
    quiz = dict(collection.find_one({"quiz num": num}))
    answers = []
    for i in range(1, 11):
        answers += [quiz["question " + str(i)]["correct answer"]]
    return "/".join(map(str, answers)) + "\n"


def getDialog(quiz_name, num, num_question, num_answer):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[dic_of_quizzes[quiz_name]]
    quiz = dict(collection.find_one({"quiz num": num}))
    question = quiz.get("question " + str(num_question))
    dialog = question.get("question") + "/"
    if num_answer == 0:
        dialog += "לא סימנת תשובה"
    else:
        dialog += question.get("answers").get("answer " + str(num_answer))
    dialog += "/" + question.get("answers").get("answer " + question.get("correct answer")) + "\n"
    return dialog


def what_quizzes_left(id_num):
    connection1 = pymongo.MongoClient('localhost', 27017)
    database1 = connection1["userS"]
    collection1 = database1["students"]
    connection2 = pymongo.MongoClient('localhost', 27017)
    database2 = connection2["userT"]
    collection2 = database2["teachers"]
    student = collection1.find_one({"_id": id_num})
    school = student.get("school")
    complete = student.get("quizzes complete")
    assigned = collection2.find_one({"school": school}).get("assigned quizzes")
    left = []
    for i in assigned:
        if i not in complete:
            left += i
    print(left)


def is_signIn(info):
    connection = pymongo.MongoClient('localhost', 27017)
    details = json.loads(info)
    database = connection["userT"]
    collection = database["teachers"]
    if collection.count_documents({"_id": details.get("id")}, limit=1) == 1:
        if collection.find_one({"_id": details.get("id")}).get("password") != details.get("password"):
            return "false"
        return "teacher"
    database = connection["userS"]
    collection = database["students"]
    if collection.count_documents({"_id": details.get("id")}, limit=1) == 1:
        if collection.find_one({"_id": details.get("id")}).get("password") != details.get("password"):
            return "false"
        return "student"
    return "false"


def get_info(data):
    connection = pymongo.MongoClient('localhost', 27017)
    details = json.loads(data)
    database = connection["userT"]
    collection = database["teachers"]
    job = "מורה"
    if collection.count_documents({"_id": details.get("id")}, limit=1) == 0:
        database = connection["userS"]
        collection = database["students"]
        job = "תלמיד"
    dic = collection.find_one({"_id": details.get("id")})
    return dic.get("full name") + "," + dic.get("school") + "," + dic.get("email") + "," + job


def get_amount_of_questions(quiz_name):
    if quiz_name not in dic_of_quizzes:
        return 0
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection["quizzes"]
    collection = database[dic_of_quizzes[quiz_name]]
    return str(collection.count_documents({})) + "\n"


def get_collections(quiz_name):
    connection = pymongo.MongoClient('localhost', 27017)
    database = connection[quiz_name]
    names = ""
    for i in dic_of_quizzes2[quiz_name]:
        names += i + "/"
    return names + "\n"


if __name__ == '__main__':
    while True:
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.bind((SERVER_IP, DEST_PORT))
        server_socket.listen(1)
        client_socket, address = server_socket.accept()
        data = client_socket.recv(2048).decode()
        print(data)
        #data = crack(data, 2) #רק בהרשמה
        info = json.loads(data)
        if info.get("function") == "signUp":
            if sign_up(data):
                client_socket.sendall(encryption("true", 3).encode())
            else:
                client_socket.sendall(encryption("false", 3).encode())
        elif info.get("function") == "signIn":
            what = is_signIn(data)
            print(what)
            if what == "teacher" or what == "student":
                client_socket.sendall("true\n".encode())
            else:
                client_socket.sendall("false\n".encode())
        elif info.get("function") == "getInfo":
            print(get_info(data))
            infoS = get_info(data) + "\n"
            client_socket.sendall(infoS.encode())
        elif info.get("function") == "getQuiz":
            connection = pymongo.MongoClient('localhost', 27017)
            database = connection["quizzes"]
            collection = database[dic_of_quizzes[info.get("quiz")]]
            dic1 = collection.find_one({"quiz num": info.get("quiz num")})
            print(dic1)
            print(info.get("question num"))
            dic2 = dic1.get("question " + str(info.get("question num")))
            print(dic2)
            question = dic2.get("question") + "/" + dic2.get("answers").get("answer 1") + "/" + dic2.get("answers").get(
                'answer 2') + "/" + dic2.get("answers").get("answer 3") + "/" + dic2.get("answers").get("answer 4") + "\n"
            client_socket.sendall(question.encode())
        elif info.get("function") == "getAnswers":
            print(get_answers(info.get("quiz"), info.get("quiz num")))
            client_socket.sendall(get_answers(info.get("quiz"), info.get("quiz num")).encode())
        elif info.get("function") == "dialog":
            print(info)
            print(getDialog(info.get("quiz"), info.get("quiz num"), info.get("question num"), info.get("answer")))
            client_socket.sendall(getDialog(info.get("quiz"), info.get("quiz num"),
                                            info.get("question num"), info.get("answer")).encode())
        elif info.get("function") == "howManyQuizzes":
            client_socket.sendall(get_amount_of_questions(info.get("quiz_name")).encode())
        elif info.get("function") == "getCollections":
            client_socket.sendall(get_collections(info.get("quiz")).encode())
        elif info.get("function") == "completeQuiz":
            complete_quiz(info.get("quiz"), info.get("quiz num"), info.get("id"))
            client_socket.sendall("true".encode())

        client_socket.close()
        server_socket.close()


