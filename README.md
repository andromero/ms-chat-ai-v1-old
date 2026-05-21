Business Rules

#Name
The name of chat request must follow these rules:

##Validation Rules

###1 - Microservice
a. Must start with the prefix ms-
b. Must contain letters (upper and lower cases with special latin characters), numbers, or hyphens (-) with up to 80 characters (without prefix and suffix) and is mandatory. Cannot start with a hyphen or number.
c. Must end with the suffix -v and the version number with up to 3 digits is mandatory.
example: ms-teste-v1

###2 - API
a. Must start with the prefix api-
b. Must contain letters, numbers (upper and lower cases with special latin characters), or hyphens (-) with up to 80 characters (without prefix and suffix) and is mandatory. Cannot start with a hyphen or number.
c. Must end with the suffix -v and the version number with up to 3 digits is mandatory.
example: api-teste-v1

###3 - LIB
a. Must start with the prefix lib-
b. Must contain letters (upper and lower cases with special latin characters), numbers, or hyphens (-) with up to 80 characters (without prefix and suffix) and is mandatory. Cannot start with a hyphen or number.
c. Could have a suffix -v and the version number with up to 3 digits.
example: lib-cao-v1 or lib-cao.

#UUID
The uuid of chat request must follow these rules:
a. must be a valid UUID format.