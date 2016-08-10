<?php

//get the user id
$id = $_POST['id'];

//Importing Database Script 
require_once('dbConnect.php');
	
//Creating sql query
$sql = "SELECT c.ime, c.prezime, c.datum, c.visina, c.tezina, d.naziv, s.rezultat 
		FROM StavkeTreninga AS s 
		INNER JOIN Clan as c ON c.id = s.id_clan 
		INNER JOIN Disciplina AS d ON d.id = s.id_disciplina
		WHERE s.id_clan = '$id' 
		ORDER BY d.id, s.rezultat";
	
$r = mysqli_query($con,$sql);
	
$result = array();
	
//looping through all the records fetched
while($row = mysqli_fetch_array($r, MYSQL_ASSOC)){
	array_push($result,array(
		"ime"=>$row['ime'],
		"prezime"=>$row['prezime'],
		"datum"=>$row['datum'],
		"visina"=>$row['visina'],
		"tezina"=>$row['tezina'],
		"naziv"=>$row['naziv'],
		"rezultat"=>$row['rezultat']
	));
}

echo json_encode($result);
	
mysqli_close($con);