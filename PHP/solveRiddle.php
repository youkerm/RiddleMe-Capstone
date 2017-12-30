<?php
include("database.php");

$riddleID = $_GET["riddle_id"];
$userID = 'ca4ff8e6-0027-4769-a3f1-f385e4e0310a'; //Hard coded for now
$answer = $_GET["answer"];

$db = new Database();
$sql = "SELECT answer FROM riddles WHERE id=?";

$query = $db->getConnection()->prepare($sql);
$query->execute(array($riddleID));

$row = $query->fetch(PDO::FETCH_ASSOC);
if ($answer == $row['answer']) {
	if ($riddleID != null && $userID != null) {
		$sql = "INSERT INTO solved_riddles(user_id, riddle_id) VALUES (?, ?)";

		$query = $db->getConnection()->prepare($sql);
		$query->execute(array($userID, $riddleID));
		echo json_encode("success");
	} else {
		echo json_encode(array('error' => 'User ID and the riddle ID is required!'));
	}
} else {	
	echo json_encode(array('error' => 'Wrong answer!'));
}


?>