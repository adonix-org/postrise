
module org.adonix.postrise {

    requires com.zaxxer.hikari;
    requires transitive java.sql;
    requires org.apache.logging.log4j;

    exports org.adonix.postrise;
    exports org.adonix.postrise.security;
    exports org.adonix.postrise.exception;
}
