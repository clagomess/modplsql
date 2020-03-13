CREATE OR REPLACE FUNCTION GET_PARAM (
    P_NUM_ENTRIES IN NUMBER DEFAULT 0,
    P_NAME_ARRAY IN OWA.VC_ARR,
    P_VALUE_ARRAY IN OWA.VC_ARR,
    P_VALUE IN VARCHAR2,
    P_DEFAULT IN VARCHAR2 DEFAULT NULL
) RETURN VARCHAR2 IS
V_RETVAL VARCHAR2(1024);
V_IDX NUMBER(5);
BEGIN
  V_RETVAL := P_DEFAULT;
  FOR V_IDX IN 1..P_NUM_ENTRIES LOOP
    IF (UPPER(P_VALUE) = UPPER(P_NAME_ARRAY(V_IDX))) THEN
      V_RETVAL := P_VALUE_ARRAY(V_IDX);
      RETURN V_RETVAL;
     END IF;
   END LOOP;
   RETURN V_RETVAL;
END;