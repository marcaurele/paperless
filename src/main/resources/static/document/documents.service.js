angular.module('documents').factory('Documents', [ '$resource',
	function($resource) {
		return $resource('/repositories/:repositoryId/documents/:documentId', {}, {
			list : {
				method : 'GET',
				params : {
					repositoryId : '@repositoryId'
				},
				isArray : true
			},
			get : {
				method : 'GET',
				params : {
					repositoryId : '@repositoryId',
					documentId : '@documentId'
				}
			},
			update : {
				method : 'PUT',
				params : {
					repositoryId : '@repositoryId',
					documentId : '@documentId'
				}
			},
			reindex : {
				method : 'POST',
				params : {
					repositoryId : '@repositoryId',
					documentId : 'reindex'
				}
			}
		});
	}
]);