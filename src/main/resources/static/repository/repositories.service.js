angular.module('repositories').factory('Repositories', [ '$resource',
	function($resource) {
		return $resource('/repositories/:repositoryId', {}, {
			list : {
				method : 'GET',
				isArray : true,
				cache: true
			},
			get : {
				method : 'GET',
				params : {
					repositoryId : "@repositoryId"
				},
				isArray : false,
				cache: true
			}
		});
	}
]);