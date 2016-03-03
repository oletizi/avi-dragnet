<html>
<head>
    <link href='https://fonts.googleapis.com/css?family=Fira+Sans' rel='stylesheet' type='text/css'>
    <style>
        body {
            font-family: 'Fira Sans', sans-serif;
        }

        #container {
            width: 50%;
            margin: auto;
        }

        a.button {
            color: white;
            font-size: 14px;
            font-weight: 700;
            background-color: #ff4b00;
            padding: 12px 16px;
            border: 2px solid #ff4b00;
            border-radius: 4px;
        }

        a:link {
            text-decoration: none;
        }
    </style>
</head>
<body>
<div id="container">
    <h1>Avi Dragnet</h1>

    <div><a class="button" href="/filtered.xml">Subscribe here&nbsp;&rsaquo;</a></div>

    <p style="margin-top: 50px">
        The Avi Dragnet is an RSS feed concatenated from multiple sources around the web, including Google Groups,
        Stack Overflow, Stack Exchange, and DZone.
    </p>
    <p>
        The concatenated feed is filtered according to various pattern matching rules.
    </p>


    <h2>Source Feeds</h2>
    <p>The following feeds are the source feeds of the Avi Dragnet.</p>
<#list feeds as feed>
    <strong>${feed.name}</strong>
    <ul>
        <li><a href="${feed.link}">Link: ${feed.link}&nbsp;&rsaquo;</a></li>
        <li><a href="${feed.localRawFeedUrl}">Raw Feed &rsaquo;</a></li>
        <li><a href="${feed.localFilteredFeedUrl}">Filtered Feed &rsaquo;</a></li>
        <li>Size: ${feed.size}</li>
        <li>Last updated: ${feed.lastUpdated}</li>
        <li>Description<br/>
        ${feed.description}
        </li>
    </ul>
    <hr>
</#list>

    <h2>Archives</h2>

    <p></p><a href="archive">Archived feeds &rsaquo;</a></p>

    <h2>Logs</h2>

    <p><a href="log.txt">Log &rsaquo;</a></p>
    <p><a href="error-log.txt">Error &rsaquo;</a></p>
</div>
</body>
</html>