angular.module('properties').component('properties', {
	templateUrl : 'document/properties.html',
	controller : [ '$routeParams', '$http', '$location', 'DescriptionTypes', function documentsController($routeParams, $http, $location, DescriptionTypes) {
		var self = this;
		
		DescriptionTypes.list({}, function(descriptionTypes) {
			self.descriptionTypes = descriptionTypes;
		});

		self.range = function(descriptionValues) {
			var input = [];
			var min = 0;
			var max = 0;

			if (descriptionValues) {
				max = descriptionValues.length - 1;
			}

			for (var i = min; i <= max; i += 1) {
				input.push(i);
			}

			return input;
		};

		self.init = function(descriptionType) {
			if (!self.descriptions[descriptionType]) {
				self.descriptions[descriptionType] = [ '' ];
			}
		};

		self.clear = function(descriptionType, n) {
			if (self.descriptions[descriptionType] && self.descriptions[descriptionType].length > n) {
				self.descriptions[descriptionType][n] = '';
			}
		};

		self.addElement = function(descriptionType) {
			if (!self.descriptions[descriptionType]) {
				self.descriptions[descriptionType] = [ '', '' ];
			} else {
				self.descriptions[descriptionType].push('');
			}
		};

		self.removeElement = function(descriptionType, n) {
			if (self.descriptions[descriptionType] && self.descriptions[descriptionType].length > n && self.descriptions[descriptionType].length > 1) {
				self.descriptions[descriptionType].splice(n, 1);
			}
		};
	} ],
	bindings : {
		descriptions : '<',
		contents : '<'
	}
});