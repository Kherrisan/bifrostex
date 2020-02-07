package cn.kherrisan.bifrostex_client.core.common

@Open
data class RuntimeConfiguration(
        var proxyHost: String? = null,

        var proxyPort: Int? = null,

        /**
         * api key
         * or access key
         */
        var apiKey: String? = null,

        var secretKey: String? = null,

        var username: String? = null,

        /**
         * password
         * or passphrase
         */
        var password: String? = null,

        var pemPath: String? = null,

        var pingInterval: Int? = null,

        var pingTimeout: Int? = null
)