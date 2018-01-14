angular.module('scanner').component('scanner', {
	templateUrl : 'scanner/scanner.html',
	controller : [ '$http', '$location', function scannerController($http, $location) {
		var self = this;

		self.scanOptions = {
			source : "",
			number : 1
		};

		$http({
			method : 'GET',
			url : '/scannerSources'
		}).then(function successCallback(response) {
			self.scannerSources = response.data;
			self.scanOptions.source = self.scannerSources[0];
		}, function errorCallback(response) {
			alert('Unable to load scanner sources');
		});

		// scan button
		self.callScan = function() {
			$http.post('/scans', JSON.stringify(self.scanOptions)).then(function(response) {
				if (response.data.success) {
					$location.path('/');
				}
			}, function(reason) {
				console.log('KO: ' + reason);
			});
		};
	} ]
});