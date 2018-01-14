angular.module('descriptionIndex').factory('DescriptionIndex', [ '$resource',
	function($resource) {
		return $resource('/indexes/:type', {}, {
			list : {
				method : 'GET',
				isArray : true
			},
			get : {
				method : 'GET',
				params : {
					type : '@type'
				}
			}
		});
	}
]);