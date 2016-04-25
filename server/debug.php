<?php
    $sess = json_decode(utf8_encode(file_get_contents("sessions.json")));
    $users = json_decode(utf8_encode(file_get_contents("users.json")));

    echo 'Sessions:<br>';
    var_dump($sess);
    echo '<br>Users:<br>';
    var_dump($users);

    if (isset($_GET["logout"])) {
        file_put_contents('sessions.json', '{}');
        echo '<br>Sessions cleared.<br>';
    }
    if (isset($_GET["deleteall"])) {
        file_put_contents('users.json', '{}');
        echo '<br>Users cleared.<br>';
    }
?>