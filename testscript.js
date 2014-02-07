var fs = require('fs');
var exec = require('child_process').exec;


exec('start cmd', function(err, stdout, stderr) {
    if (err) {
        console.log('child process exited with error code ' + err);
        return;
    }
    console.log(stdout);
});