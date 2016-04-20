<?php
    if (isset($_GET["key"]) && isset($_GET["message"]) && !empty($_GET["key"])) {
        $key = $_GET["key"];
        $text = $_GET["message"];
        $sess = json_decode(utf8_decode(file_get_contents("sessions.json")));
        $usn = $sess->{$key};
        if (!empty($usn)) {
            file_put_contents($usn . ".note", $text);
        } else {
            header('HTTP/1.0 403 Forbidden');
        }
    } else if (isset($_GET["key"]) && !empty($_GET["key"])) {
        $key = $_GET["key"];
        $sess = json_decode(utf8_decode(file_get_contents("sessions.json")));
        $usn = $sess->{$key};
        if (!empty($usn)) {
            if (file_exists($usn . ".note")) {
                $note = file_get_contents($usn . ".note");
            } else {
                $note = "";
            }
            echo $note;
        }
    } else {
        header('HTTP/1.0 400 Bad Request');
    }
?>