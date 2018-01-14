angular.module('paperless').config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/search/:repositoryId', {
		templateUrl : 'document/search.html',
	}).when('/edit/:repositoryId/:documentId*', {
		templateUrl : 'document/edit.html'
	}).otherwise({
		redirectTo : '/search/incoming'
	});
} ]);