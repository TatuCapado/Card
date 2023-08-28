INSERT INTO CLIENT(first_name,last_name) VALUES('Dan','Grigore');
INSERT INTO CLIENT(first_name,last_name) VALUES('Andrei','Iancu');
INSERT INTO CLIENT(first_name,last_name) VALUES('Alexandra','Gigica');
INSERT INTO CLIENT(first_name,last_name) VALUES('Dan','Chiru');
INSERT INTO CLIENT(first_name,last_name) VALUES('test1','test2');


INSERT INTO CARD(card_money,expire_on,client_id) VALUES(23,null,3);
INSERT INTO CARD(card_money,expire_on,client_id) VALUES(10,CURDATE() + INTERVAL 30 DAY,4);
INSERT INTO CARD(card_money,expire_on,client_id) VALUES(10,STR_TO_DATE('20-05-2018', '%d-%m-%Y'),2);
INSERT INTO CARD(card_money,expire_on,client_id) VALUES(50,null,7);

INSERT INTO TRANSPORT(line,type,charge_per_trip) VALUES(41,'tramvai',1.3);
INSERT INTO TRANSPORT(line,type,charge_per_trip) VALUES(336,'autobuz',1.3);
INSERT INTO TRANSPORT(line,type,charge_per_trip) VALUES(783,'express',3);

INSERT INTO CARD_TYPE(pass_type,price,card_id) VALUES('abonament-lunar',25,7);
INSERT INTO CARD_TYPE(pass_type,price,card_id) VALUES('abonament-zi',10,13);
INSERT INTO CARD_TYPE(pass_type,price,card_id) VALUES('recharge',null,10);
INSERT INTO CARD_TYPE(pass_type,price,card_id) VALUES('recharge',null,17);

INSERT INTO VALIDATION(card_id,transport_id) VALUES(10,1);

DELETE FROM CARD WHERE card_id = 12;

DELETE FROM VALIDATION WHERE id = 3;

SELECT * FROM CARD_TYPE;

SELECT * FROM CLIENT cl, CARD cr, CARD_TYPE ct, VALIDATION v
WHERE cl.client_id = cr.client_id
AND cr.card_id = ct.card_id
AND v.card_id = cr.card_id;

SELECT v.`date&time`, t.line FROM VALIDATION v, TRANSPORT t WHERE card_id = 17 AND v.transport_id = t.transport_id;


SELECT *
FROM CLIENT cl, CARD cr, CARD_TYPE ct
WHERE cl.first_name ="Gigel" 
	  AND cl.last_name = "Frone" 
      AND cl.client_id = cr.client_id
      AND ct.card_id = cr.card_id;

SELECT * FROM CARD_TYPE;

UPDATE CARD_TYPE SET pass_type = "abonament-lunar", price = 25 WHERE id = 12;

SELECT * FROM CLIENT;

SELECT * FROM CARD;

UPDATE CARD SET card_money = card_money + 1 WHERE client_id = (SELECT client_id FROM CLIENT WHERE first_name = 'Alexandra' AND last_name = 'Gigica');

INSERT INTO VALIDATION(card_id,transport_id,`date&time`) VALUES(?,?,CURRENT_TIMESTAMP());

SELECT * FROM CLIENT;
SELECT * FROM CARD;
SELECT * FROM TRANSPORT;
SELECT * FROM CARD_TYPE;
SELECT * FROM VALIDATION;

DELETE FROM CARD_TYPE WHERE type_id = 22;
DELETE FROM CARD WHERE card_id = 26;
DELETE FROM CLIENT WHERE client_id = 19;

SELECT * FROM CLIENT c, CARD  cs
where c.client_id = cs.client_id AND c.first_name = 'Dan' AND c.last_name = 'Andrei'
ORDER BY first_name;

SELECT * FROM CLIENT c, CARD  cs, TRANSPORT t, VALIDATION v, CARD_TYPE ct
where c.client_id = cs.client_id AND t.transport_id = v.transport_id AND v.card_id = cs.card_id AND ct.card_id = cs.card_id
ORDER BY first_name;

SELECT * FROM CLIENT c, CARD  cs
where c.client_id = cs.client_id 
ORDER BY cs.card_id DESC;

SELECT * FROM CARD c, CLIENT cl WHERE c.client_id = cl.client_id AND cl.first_name = 'Dan' AND cl.last_name = 'Grigore';

SELECT * FROM CARD c, CLIENT cl WHERE c.client_id = cl.client_id AND cl.first_name = 'Dan' AND cl.last_name = 'Grigore';

UPDATE CARD SET card_money = card_money + 1 WHERE client_id = (SELECT client_id FROM CLIENT WHERE first_name = 'Alexandra' AND last_name = 'Gigica');

ROLLBACK;