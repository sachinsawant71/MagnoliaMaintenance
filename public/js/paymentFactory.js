'use strict';

var services = angular.module('magnolia.services', ['ngResource']);

services.factory('Apartments', function ($resource) {
    return $resource('/api/apartments/:id', {}, {});
});

services.factory('Payments', function ($resource) {
    return $resource('/api/payments/:id', {}, {});
});

