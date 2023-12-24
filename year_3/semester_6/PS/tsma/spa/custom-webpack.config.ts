import { EnvironmentPlugin } from 'webpack';

require('dotenv').config();

module.exports = {
	plugins: [
		new EnvironmentPlugin([
			'PORT',
			'API_URL',
			'AZURE_CLIENT_ID'
		])
	]
}
