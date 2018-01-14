angular.module('documents').component('documents', {
	templateUrl : 'document/documents.html',
	controller : [ '$routeParams', 'Documents', function documentsController($routeParams, Documents) {
		var self = this;

		self.repositoryId = $routeParams.repositoryId;
		self.repositoryIdParams = {
			repositoryId : self.repositoryId
		};
		self.descriptions = {};

		self.reindex = function() {
			Documents.reindex(self.repositoryIdParams, function(documents) {
				alert('finished');
			});
		};

		self.search = function() {
			self.filter = Object.assign({}, self.descriptions, self.repositoryIdParams);
			Documents.list(self.filter, function(documents) {
				self.documents = documents;
			});
		};

		self.$onInit = function() {
			self.search();
		};
	} ]
});