angular.module('document').component('document', {
	templateUrl : 'document/document.html',
	controller : [ '$routeParams', '$http', '$location', 'Documents', function documentsController($routeParams, $http, $location, Documents) {
		var self = this;

		Documents.get({
			repositoryId : $routeParams.repositoryId,
			documentId : $routeParams.documentId
		}, function(document) {
			self.document = document;
			$('#embedDocument').html(`<embed class="embed-responsive-item" src="${document._links.file.href}" type="application/pdf"></embed>`);
		});

		self.update = function(archive) {
			Documents.update({
				repositoryId : $routeParams.repositoryId,
				documentId : $routeParams.documentId,
				archive : archive
			}, self.document, function(document) {
				if (archive) {
					$location.path('/');
				} else {
					$location.path(`/edit/${$routeParams.repositoryId}/${document.documentId}`);
				}
			});
		};
	} ]
});