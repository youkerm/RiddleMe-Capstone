<?php
include("database.php");

$user_id = 'ca4ff8e6-0027-4769-a3f1-f385e4e0310a'; //Hard coded for now
$riddle_text = $_GET['riddle_text'];
$riddle_answer = $_GET['riddle_answer'];
$riddle_lat = $_GET['riddle_lat'];
$riddle_long = $_GET['riddle_long'];

$db = new Database();
$sql = "INSERT INTO riddles(riddle, answer, user_id, location) VALUES (?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326) );";

$query = $db->getConnection()->prepare($sql);
$query->execute(array($riddle_text, $riddle_answer, $user_id, $riddle_long, $riddle_lat));

$row = $query->fetch(PDO::FETCH_ASSOC);