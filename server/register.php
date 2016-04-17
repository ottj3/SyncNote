<?php
    if (isset($_GET["username"]) && isset($_GET["password"])
        && !empty($_GET["username"]) && !empty($_GET["password"])) {
        $usn = $_GET["username"];
        $pass = $_GET["password"];
        $db = json_decode(utf8_encode(file_get_contents("users.json")));
        vardump($db); // for debugging, really need to set up phpstorm again...

        if (empty($db->$usn))
            $db->$usn = $pass;
            $b = file_put_contents("users.json", json_encode($db));
            echo 'User created. (db size: ' . $b . ')';
        } else {
            echo 'User already exists.';
        }
    }
?>