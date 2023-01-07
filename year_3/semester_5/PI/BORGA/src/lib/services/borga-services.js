'use strict'

const config = require('./borga-services-config')

module.exports = (data_ext, data_in) => {

    let services = {}

    Object.assign(services, require('./games.service')(data_ext, config.getGamesQueryConfig))
    Object.assign(services, require('./groups.service')(data_ext, data_in))
    Object.assign(services, require('./users.service')(data_in))

    return services
}