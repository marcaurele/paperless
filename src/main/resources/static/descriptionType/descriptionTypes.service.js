angular.module('descriptionTypes').factory('DescriptionTypes', [ '$resource',
	function($resource) {
		return $resource('/descriptionTypes', {}, {
			list : {
				method : 'GET',
				isArray : true,
				cache : true
			}
		});
	}
]);