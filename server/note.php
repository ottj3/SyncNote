<?php
    if (isset($_POST["key"]) && isset($_POST["message"] && !empty($_POST["key"]))) {
        $key = $_POST["key"];
        $text = $_POST["message"];
        // TODO get user id from login token
        file_put_contents($key . ".note", $message);
        echo 'Received upload'.
    } else if (isset($_GET["key"]) && !empty($_GET["key"])) {
        // TODO get user id from login token
        echo file_get_contents($_GET["key"] . ".note");
    } else {
        echo 'Invalid request.';
    }
?>