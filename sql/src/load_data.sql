/* Replace the location to where you saved the /home/csmajs/sduvv003/cs166_project_phase3/data files*/
COPY Users
FROM '/home/csmajs/nvank001/CS166-Final-Project/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Items
FROM '/home/csmajs/nvank001/CS166-Final-Project/data/items.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Store
FROM '/home/csmajs/nvank001/CS166-Final-Project/data/store.csv'
WITH DELIMITER ',' CSV HEADER;

COPY FoodOrder
FROM '/home/csmajs/nvank001/CS166-Final-Project/data/foodorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY ItemsInOrder
FROM '/home/csmajs/nvank001/CS166-Final-Project/data/itemsinorder.csv'
WITH DELIMITER ',' CSV HEADER;
