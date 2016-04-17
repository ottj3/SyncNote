<?php
    if (isset($_GET["username"]) && isset($_GET["password"])
        && !empty($_GET["username"]) && !empty($_GET["password"])) {
        $usn = $_GET["username"];
        $pass = $_GET["password"];
        $db = json_decode(utf8_encode(file_get_contents("users.json")));
        vardump($db); // for debugging, need to set up phpstorm...
        if (!empty($db->{$usn}) && strcmp($db->{$usn}, $pass) == 0) {
            echo 'Logged in.' // TODO add session via login token
        }
    }
?>