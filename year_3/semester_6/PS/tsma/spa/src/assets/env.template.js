(function (window) {
	window['env'] = window['env'] || {};

	// Environment variables
	window['env']['apiUrl'] = '${API_URL}';
	window['env']['msalClientId'] = '${AZURE_CLIENT_ID}';
})(this);
