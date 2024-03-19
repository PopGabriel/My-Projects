import sys
import subprocess
import platform
import matplotlib.pyplot as plt
import numpy as np
import psycopg2
import psutil

def get_processor_info():
    try:
        result = subprocess.run(['wmic', 'cpu', 'get', 'name'], stdout=subprocess.PIPE, text=True, check=True)
        lines = result.stdout.strip().split('\n')

        lines = lines[1:]

       
        processor_name = ' '.join(line.strip() for line in lines)

        cores = psutil.cpu_count(logical=False)

        return f"{processor_name} ({cores} cores)"
    except subprocess.CalledProcessError as e:
        print("Error running 'wmic' command:", e)
      
        info = platform.processor()
        cores = psutil.cpu_count(logical=False)

        return f"{info} ({cores} cores)"

def insert_data_into_postgres(processor_model, test_number, score):
    try:
        conn = psycopg2.connect(
            user="postgres",
            password="",
            host="localhost",
            port="5432",
            database="scs"
        )
        cursor = conn.cursor()

        cursor.execute(
            'INSERT INTO "SCSdata" ("Processor_model", "Test_number", "Score") VALUES (%s, %s, %s)',
            (processor_model, test_number, score)
        )

        conn.commit()
        cursor.close()
        conn.close()
        print("Data inserted into PostgreSQL successfully!")

    except (Exception, psycopg2.Error) as error:
        print("Error while connecting to PostgreSQL or inserting data:", error)

processor_model = get_processor_info()
score = float(sys.argv[2])
test_number = int(sys.argv[3])

insert_data_into_postgres(processor_model, test_number, score)
filename = sys.argv[1]
data = np.genfromtxt(filename, delimiter=',')

x = data[:, 0]
y = data[:, 1]

plt.plot(x, y, marker='o', linestyle='-', color='b')
plt.xlabel('Number of threads')
plt.ylabel('Execution time(s)')
plt.title('Execution time to number of threads')

plt.xscale('log', base=2)

plt.show()
