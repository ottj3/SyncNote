<?php
    if (isset($_GET["username"]) && isset($_GET["password"])
        && !empty($_GET["username"]) && !empty($_GET["password"])) {

        $usn = $_GET["username"];
        $pass = $_GET["password"];
        $db = json_decode(utf8_encode(file_get_contents("users.json")));

        if (empty($db->$usn)) {
            $db->$usn = $pass;
            $b = file_put_contents("users.json", json_encode($db));
            header('HTTP/1.0 200 OK');
        } else {
            header('HTTP/1.0 403 Forbidden');
        }
    } else {
        header('HTTP/1.0 400 Bad Request');
    }
?>