import React, {Component} from 'react';

export default class Book extends Component {
    clickHandler = (title) => {
        this.props.update(title);
    };

    render = () => {
        return (
            <>
                <article
                    className={`message ${this.props.active ? 'is-success' : 'is-info'}`}
                    onClick={e => this.clickHandler(this.props.title)}>
                    <div className="message-body">
                        { this.props.title }
                    </div>
                </article>
            </>
        );
    };
}
