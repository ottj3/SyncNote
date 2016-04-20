<?php
    if (isset($_GET["username"]) && isset($_GET["password"])
        && !empty($_GET["username"]) && !empty($_GET["password"])) {
        $usn = $_GET["username"];
        $pass = $_GET["password"];
        $db = json_decode(utf8_encode(file_get_contents("users.json")));
        if (!empty($db->{$usn}) && strcmp($db->{$usn}, $pass) == 0) {
            $token = uniqid(); // yes, this is not secure
            $sess = json_decode(utf8_encode(file_get_contents("sessions.json")));
            $sess->{$token} = $usn;
            file_put_contents("sessions.json", json_encode($sess));
            echo $token;
        } else {
            header('HTTP/1.0 403 Forbidden');
        }
    } else {
        header('HTTP/1.0 400 Bad Request');
    }
?>