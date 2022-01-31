from enum import Enum

class GoflexAction(Enum):
    CREATE_ORDER = "CREATE_ORDER"
    CREATE_PRODUCT = "CREATE_PRODUCT"
    CREATE_PERSON = "CREATE_PERSON"
    EDIT_ORDER = "EDIT_ORDER"


print(type(GoflexAction.CREATE_ORDER.value))