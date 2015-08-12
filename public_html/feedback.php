<?php
require_once "connect_database.php";

$obj = new Connect_database();
$conn = $obj->connect_db();

$TABLE_NAME = "feedback";
$res = "";
if (key_exists('response', $_GET)){
	$res = $_GET['response'];
}
if (strlen($res) > 0){
	$v = date_create();
	$ts = $v->format('Y-m-d H:i:s');
	$q = "INSERT INTO ".$TABLE_NAME. " (response, ts) VALUES ('$res', '$ts')";
	mysql_query($q, $conn);
}
?>