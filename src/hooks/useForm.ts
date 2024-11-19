import {useState, useCallback} from 'react';

type ValidateFunction<T> = (values: T) => Partial<T>;

function useForm<T extends Record<string, any>>({
  initialValue,
  validate,
}: {
  initialValue: T;
  validate: ValidateFunction<T>;
}) {
  const [values, setValues] = useState<T>(initialValue);
  const [errors, setErrors] = useState<Partial<T>>({});
  const [touched, setTouched] = useState<Partial<Record<keyof T, boolean>>>({});

  const handleChange = useCallback(
    (name: keyof T) => (value: string) => {
      setValues(prev => ({...prev, [name]: value}));
      setErrors(prev => ({...prev, [name]: ''}));
    },
    [],
  );

  const setValue = useCallback(
    (name: keyof T, value: string) => {
      setValues(prev => ({...prev, [name]: value}));
      setErrors(prev => ({...prev, [name]: ''}));
    },
    [],
  );

  const handleBlur = useCallback(
    (name: keyof T) => () => {
      setTouched(prev => ({...prev, [name]: true}));
      const validationErrors = validate(values);
      setErrors(prev => ({...prev, ...validationErrors}));
    },
    [values, validate],
  );

  const getTextInputProps = useCallback(
    (name: keyof T) => ({
      value: values[name] as string,
      onChangeText: handleChange(name),
      onBlur: handleBlur(name),
    }),
    [values, handleChange, handleBlur],
  );

  return {
    values,
    errors,
    touched,
    getTextInputProps,
    setErrors,
    setValue,
    handleBlur,
  };
}

export default useForm;
