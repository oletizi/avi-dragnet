"use strict";
var page = require('webpage').create();

page.settings.userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";
page.settings.loadImages = false;
page.settings.resourceTimeout = 30 * 1000;
page.settings.webSecurityEnabled = false;

page.viewportSize = {
    width: 480,
    height: 800
};

page.onUrlChanged = function (targetUrl) {
    console.log('New URL: ' + targetUrl);
};

page.onResourceTimeout = function (request) {
    console.log('Resource timeout: Response (#' + request.id + '): ' + JSON.stringify(request));
};

page.onResourceError = function (request) {
    console.log('Unable to load resource (#' + resourceError.id + 'URL:' + resourceError.url + ')');
    console.log('Error code: ' + resourceError.errorCode + '. Description: ' + resourceError.errorString);
};

page.onError = function (msg, trace) {

    var msgStack = ['ERROR: ' + msg];

    if (trace && trace.length) {
        msgStack.push('TRACE:');
        trace.forEach(function (t) {
            msgStack.push(' -> ' + t.file + ': ' + t.line + (t.function ? ' (in function "' + t.function + '")' : ''));
        });
    }

    console.error(msgStack.join('\n'));

};

page.onConsoleMessage = function (msg) {
    console.log(msg);
};

page.onLoadFinished = function () {
    var outfile = "/tmp/page.png";
    var waitTime = 10 * 1000;

    console.log("Waiting for " + waitTime + "ms before rendering.");
    setTimeout(function () {
        console.log("Rendering page to: " + outfile);
        page.render(outfile);
        console.log("Page rendering complete.");
        console.log("Digging for dates...");
        digForDates(page);
    }, waitTime);
};

page.open("${entry.url}", function (status) {
});

function digForDates(page, callback) {
    page.includeJs("https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js", function () {
        page.evaluate(function () {
            $(".IVILX2C-tb-Q").each(function () {
                console.log("IVILX2C-tb-Q: title: " + $(this).attr("title") + ", text" + $(this).text());
            });
        });
    });
}