<?php
include("database.php");

$email = $_GET["email"];
$password = $_GET["password"];

$db = new Database();

$sqlUser = "SELECT * FROM users WHERE email=? AND password=?";
$queryUser = $db->getConnection()->prepare($sqlUser);
$queryUser->execute(array($email, $password));
$row = $queryUser->fetch(PDO::FETCH_ASSOC);

$user = array();
$user['id'] = $row['id'];
$user['email'] = $row['email'];
$user['first_name'] = $row['first_name'];
$user['last_name'] = $row['last_name'];


$sqlRiddles = "SELECT COUNT(*) as solved FROM user_riddles WHERE user_id=?";

$queryRiddles = $db->getConnection()->prepare($sqlRiddles);
$queryRiddles->execute(array($user['id']));

$row2 = $queryRiddles->fetch(PDO::FETCH_ASSOC);
$user['riddles_solved'] = intval($row2['solved']);
$user['created'] = $row['created'];

echo json_encode(array('user' => $user));

?>