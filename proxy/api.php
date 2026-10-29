<?php

date_default_timezone_set('Europe/Moscow');

const SERVICES = [
    'vehicles' => [
        'base' => 'https://localhost:13232/api/v1',
        'cert' => 'vehicles.crt',
        'name' => 'Сервис коллекции',
    ],
    'shop' => [
        'base' => 'https://localhost:13233/api/v1/shop',
        'cert' => 'shop.crt',
        'name' => 'Сервис автомастерской',
    ],
];

function fail(int $code, string $message): never
{
    http_response_code($code);
    header('Content-Type: application/xml');
    echo '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error><code>' . $code . '</code><message>'
        . htmlspecialchars($message, ENT_XML1) . '</message><time>' . date('Y-m-d\TH:i:sP') . '</time></error>';
    exit;
}

function rewriteLocation(string $location, string $self): string
{
    foreach (SERVICES as $key => $service) {
        if (str_starts_with($location, $service['base'])) {
            return $self . '/' . $key . substr($location, strlen($service['base']));
        }
    }
    return $location;
}

$uri = strtok($_SERVER['REQUEST_URI'], '?');
$script = '/' . basename(__FILE__);
$position = strpos($uri, $script);
if ($position === false) {
    fail(404, 'Ресурс не найден');
}
$self = substr($uri, 0, $position + strlen($script));

if (!preg_match('#^/(vehicles|shop)(/.*)?$#', substr($uri, strlen($self)), $match)) {
    fail(404, 'Ресурс не найден');
}
$service = SERVICES[$match[1]];
$target = $service['base'] . ($match[2] ?? '');
if (($_SERVER['QUERY_STRING'] ?? '') !== '') {
    $target .= '?' . $_SERVER['QUERY_STRING'];
}

$headers = [];
foreach (['HTTP_ACCEPT' => 'Accept', 'CONTENT_TYPE' => 'Content-Type'] as $key => $name) {
    if (!empty($_SERVER[$key])) {
        $headers[] = $name . ': ' . $_SERVER[$key];
    }
}

$http = [
    'method' => $_SERVER['REQUEST_METHOD'],
    'header' => $headers,
    'ignore_errors' => true,
    'follow_location' => 0,
    'timeout' => 30,
];
$body = file_get_contents('php://input');
if ($body !== '' && $body !== false) {
    $http['content'] = $body;
}

$context = stream_context_create([
    'http' => $http,
    'ssl' => [
        'verify_peer' => true,
        'verify_peer_name' => true,
        'peer_name' => 'localhost',
        'cafile' => __DIR__ . '/certs/' . $service['cert'],
    ],
]);

$response = @file_get_contents($target, false, $context);
if ($response === false || empty($http_response_header)) {
    fail(503, $service['name'] . ' недоступен');
}

$status = preg_match('#^HTTP/\S+\s+(\d{3})#', $http_response_header[0], $statusMatch) ? (int) $statusMatch[1] : 502;
http_response_code($status);

foreach (array_slice($http_response_header, 1) as $line) {
    [$name, $value] = array_pad(explode(':', $line, 2), 2, '');
    $name = strtolower(trim($name));
    $value = trim($value);
    if ($name === 'content-type') {
        header('Content-Type: ' . $value);
    } elseif ($name === 'location') {
        header('Location: ' . rewriteLocation($value, $self));
    }
}

echo $response;
