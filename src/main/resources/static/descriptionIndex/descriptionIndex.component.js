angular.module('descriptionIndex').component('descriptionlist', {
	templateUrl : 'descriptionIndex/descriptionIndex.html',
	controller : [ '$routeParams', 'DescriptionIndex', function descriptionIndexController($routeParams, DescriptionIndex) {
		var self = this;

		self.$onInit = function() {
			DescriptionIndex.get({
				type : self.type
			}, function(descriptionIndex) {
				self.elements = descriptionIndex.elements;
			});
		};
	} ],
	bindings : {
		type : '<'
	}
});