var stompClient = null;


function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
}

var stompClient;

function connect() {

    var socket = new SockJS('/rx-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/suggestion', function (message) {


                JSON.parse(message.body).suggestions.forEach(function (value) {
                    console.log();

                    $("#suggestionTable").append('<tr><td>' + value + '</td></tr>');
                })

                // todo this needs to be an iteration
                $("#suggestionError").append('Error: ' + JSON.parse(message.body).errors[0].message );

            },
            function (err) {
                $("#suggestionTable").append('<tr><td colspan=3> Error:' + JSON.parse(message.body).name + '</td></tr>');
            },
            function () {
                console.log('Completed');
            });

        stompClient.subscribe('/topic/search', function (message) {


                JSON.parse(message.body).result.forEach(function (value) {
                    console.log();

                    $("#accountsTable").append('<tr><td>' + value.account.name + '</td><td>' + value.account.availableAmount + '</td></tr>');
                })


            },
            function (err) {
                $("#search").append('<tr><td colspan=3> Error:' + JSON.parse(message.body).name + '</td></tr>');
            },
            function () {
                console.log('Completed');
            });
    });
}


function suggestion() {
    stompClient.send("/topic/suggestion", {}, "{\"text\":\"Sco\"}");
}

function search() {
    stompClient.send("/topic/search", {}, "{\"text\":\"Sco\"}");

}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#search").click(function () {
        search();
    });
    $("#suggestion").click(function () {
        suggestion();
    });
});