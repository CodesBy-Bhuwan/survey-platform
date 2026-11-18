
import type { SurveyQuestion, QuestionType } from "../../types/survey";

interface QuestionEditorProps {
    question: SurveyQuestion;
    index: number;

    // Update an existing question.
    onChange: (updatedQuestion: SurveyQuestion) => void;

    // Remove this question.
    onRemove: () => void;

    // Move the question up or down.
    onMove: (direction: "up" | "down") => void;

    totalQuestions: number;
}

function QuestionEditor({
    question,
    index,
    onChange,
    onRemove,
    onMove,
    totalQuestions,
}: QuestionEditorProps) {

    // Update a specific property without changing the other properties.
    const updateField = (
        field: keyof SurveyQuestion,
        value: unknown
    ) => {
        onChange({
            ...question,
            [field]: value,
        });
    };

    // Update an individual option.
    const updateOption = (optionIndex: number, value: string) => {

        const updatedOptions = [...question.options];

        updatedOptions[optionIndex] = value;

        updateField("options", updatedOptions);
    };

    // Add a new empty option.
    const addOption = () => {
        updateField("options", [...question.options, ""]);
    };

    // Remove an option.
    const removeOption = (optionIndex: number) => {

        const updatedOptions = question.options.filter(
            (_, i) => i !== optionIndex
        );

        updateField("options", updatedOptions);
    };

    // Change question type and reset its options accordingly.
    const changeQuestionType = (type: QuestionType) => {

        const options =
            type === "SINGLE_CHOICE" || type === "MULTI_SELECT"
                ? question.options.length > 0
                    ? question.options
                    : [""]
                : [];

        onChange({
            ...question,
            type,
            options,
        });
    };

    return (
        <div className="question-editor">

            {/* Question heading and position */}
            <div className="question-editor-header">

                <h3>Question {index + 1}</h3>

                <div className="question-move-actions">

                    <button
                        type="button"
                        onClick={() => onMove("up")}
                        disabled={index === 0}
                    >
                        ↑
                    </button>

                    <button
                        type="button"
                        onClick={() => onMove("down")}
                        disabled={index === totalQuestions - 1}
                    >
                        ↓
                    </button>

                    <button
                        type="button"
                        className="remove-question"
                        onClick={onRemove}
                    >
                        Remove
                    </button>

                </div>

            </div>

            {/* Question title */}
            <label>Question title</label>

            <input
                type="text"
                value={question.title}
                placeholder="Enter your question"
                onChange={(e) => updateField("title", e.target.value)}
            />

            {/* Question type */}
            <label>Question type</label>

            <select
                value={question.type}
                onChange={(e) =>
                    changeQuestionType(e.target.value as QuestionType)
                }
            >
                <option value="TEXT">Text</option>
                <option value="SINGLE_CHOICE">Single Choice</option>
                <option value="MULTI_SELECT">Multiple Select</option>
                <option value="RATING">Rating (1–5)</option>
            </select>

            {/* Display options only for choice-based questions. */}
            {(question.type === "SINGLE_CHOICE" ||
                question.type === "MULTI_SELECT") && (

                <div className="options-section">

                    <label>Answer options</label>

                    {question.options.map((option, optionIndex) => (

                        <div className="option-row" key={optionIndex}>

                            <input
                                type="text"
                                value={option}
                                placeholder={`Option ${optionIndex + 1}`}
                                onChange={(e) =>
                                    updateOption(optionIndex, e.target.value)
                                }
                            />

                            <button
                                type="button"
                                onClick={() => removeOption(optionIndex)}
                                disabled={question.options.length === 1}
                            >
                                ✕
                            </button>

                        </div>

                    ))}

                    <button
                        type="button"
                        className="add-option-button"
                        onClick={addOption}
                    >
                        + Add option
                    </button>

                </div>
            )}

            {/* Required question setting */}
            <div className="required-setting">

                <input
                    type="checkbox"
                    id={`required-${question.id}`}
                    checked={question.required}
                    onChange={(e) =>
                        updateField("required", e.target.checked)
                    }
                />

                <label htmlFor={`required-${question.id}`}>
                    Required question
                </label>

            </div>

        </div>
    );
}

export default QuestionEditor;
