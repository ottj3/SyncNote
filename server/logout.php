<?php
    if (isset($_GET["key"]) && !empty($_GET["key"])) {
        $token = $_GET["key"];
        $sess = json_decode(utf8_encode(file_get_contents("sessions.json")));
        unset($sess->$token);
        file_put_contents("sessions.json", json_encode($sess));
    } else {
        header('HTTP/1.0 400 Bad Request');
    }
?>