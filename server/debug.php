<?php
    $sess = json_decode(utf8_encode(file_get_contents("sessions.json")));
    $users = json_decode(utf8_encode(file_get_contents("users.json")));

    echo 'Sessions:<br>';
    var_dump($sess);
    echo '<br>Users:<br>';
    var_dump($users);
?>