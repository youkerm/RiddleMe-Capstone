<?php
include("database.php");

$long = $_GET["long"];
$lat = $_GET["lat"];
$userID = 'ca4ff8e6-0027-4769-a3f1-f385e4e0310a'; //Hard coded for now

//PostGIS query explanation can be found @ https://www.compose.com/articles/geofile-everything-in-the-radius-with-postgis/
$radius = 1000; //1 Kilometer = 1000

$db = new Database();
$sql = "SELECT id, riddle, ST_AsText(location) as location, answer, created FROM riddles 
			WHERE ST_DWithin(location, ST_MakePoint(?, ?)::geography, ?) AND 
			id NOT IN (SELECT riddle_id as id FROM solved_riddles WHERE user_id = ?);";

$query = $db->getConnection()->prepare($sql);
$query->execute(array($long, $lat, $radius, $userID));

$riddles = array();

while($row = $query->fetch(PDO::FETCH_ASSOC)) {
	$entry = array();
	
	$location = str_replace('POINT(', '', $row['location']);
	$location = str_replace(')', '', $location);
	$points = explode(' ', $location);
	
	$entry['id']      = $row['id'];
	$entry['riddle']  = $row['riddle'];
	$entry['answer']  = $row['answer'];
	$entry['lat']     = $points[1];
	$entry['long']    = $points[0];
	$entry['created'] = $row['created'];
	
	array_push($riddles, $entry);
}


echo json_encode(array('riddles' => $riddles));

?>