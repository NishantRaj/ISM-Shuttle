<?php 
include "connect_database.php";

$obj = new Connect_database();
$conn = $obj->connect_db();

$table_name = "bus_route_update";

$bus_number = 0;
if (key_exists("bus", $_GET)){
	$bus_number = (int)$_GET['bus'];
}

if ($bus_number){
	$q = "SELECT ts FROM ".$table_name." WHERE idx = ". $bus_number;
	$res = mysql_query($q, $conn);
	$time_update = mysql_fetch_array($res);
	$arr = array("ts" => $time_update['ts']);
	echo json_encode($arr);
}

?>	