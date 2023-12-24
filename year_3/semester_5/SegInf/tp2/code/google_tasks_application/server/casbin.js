const { newEnforcer } = require('casbin')

async function checkPermission(user, action, object) {
    const enforcer = await newEnforcer('server/config/rbac_model.conf', 'server/config/rbac_policy.csv')
    return enforcer.enforce(user, object, action)
}

async function getRoles(user) {
    const enforcer = await newEnforcer('server/config/rbac_model.conf', 'server/config/rbac_policy.csv')
    return enforcer.getRolesForUser(user)
}

module.exports = {
    checkPermission,
    getRoles
}