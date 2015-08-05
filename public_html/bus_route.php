<?php
include "connect_database.php";
$table_name = "bus_route_1";

$obj = new Connect_database();
$conn = $obj->connect_db();
$bus_number = 0;
if (array_key_exists("bus", $_GET)){
	$bus_number = (int)$_GET['bus'];
}

if ($bus_number == 1){
	$trans = "START TRANSACTION";
	mysql_query($trans);
	$q = "SELECT latitude, longitude, ts FROM ". $table_name." WHERE 1";
	$base_data = mysql_query($q, $conn);
	$trans = "COMMIT";
	mysql_query($trans);
	$data = array();
	while ($row = mysql_fetch_array($base_data)){
		array_push($data, array("latitude" => $row['latitude'], 
					"longitude" => $row['longitude'],
					"ts" => $row['ts']));
	}
	header('Content-type: application/json');
	echo json_encode($data);
}
?>