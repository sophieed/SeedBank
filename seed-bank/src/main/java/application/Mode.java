package application;

/**
 * Enum denoting the modes the repository can be accessed in.
 */
public enum Mode {

	EDIT {
		public String toString() {
			return "Edit";
		};
	},
	ADD_NEW {
		public String toString() {
			return "Add New";
		};
	},
	VIEW {
		public String toString() {
			return "View";
		};
	},
	DELETE {
		public String toString() {
			return "Delete";
		};
	}
}
